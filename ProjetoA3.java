/* 
 * ProjetoA3
 * Aplicação Java Swing com interface usando FlatLaf
 * Sistema de autenticação baseado em código (tipo CVV), com banco SQLite.
*/

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;

// Enum que representa os dois possíveis resultados da verificação
enum ResultadoVerificacao {
    ACESSO_LIBERADO,
    ACESSO_NEGADO
}


public class ProjetoA3 extends JFrame {

    // Cores usadas na interface
    private static final Color COR_TEXTO = new Color(0x000000);
    private static final Color COR_CODIGO = new Color(0x000000);

    // Componentes da interface
    private JLabel rotuloCodigo;
    private JTextField campoEntrada;
    private JLabel rotuloEntrada;
    private BotaoInterativo botaoVerificar;
    private BotaoInterativo botaoTentarNovamente;
    private BotaoInterativo botaoAbrirBanco;
    private JLabel infoResultado;
    private PainelTemporizador painelTemporizador;

    // Controle do temporizador e código atual
    private int segundosRestantes = 60;
    private String codigo;
    private Connection conexao;
    

    // Construtor principal: inicializa a interface e lógica
    public ProjetoA3() {
    super("Autenticador de CVV");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(640, 640);
    setResizable(true);
    setLocationRelativeTo(null); // centraliza a janela

    // Painel principal da janela
    JPanel container = new JPanel(new BorderLayout());
    setContentPane(container);

    // Painel do conteúdo central
    JPanel painelCentral = new JPanel();
    painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
    painelCentral.setBorder(new EmptyBorder(30, 40, 30, 40));
    container.add(painelCentral, BorderLayout.CENTER);

    // Exibe o código gerado dinamicamente
    rotuloCodigo = new JLabel("Código Gerado:");
    rotuloCodigo.setForeground(COR_CODIGO);
    rotuloCodigo.setFont(rotuloCodigo.getFont().deriveFont(Font.BOLD, 48f));
    rotuloCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);
    painelCentral.add(rotuloCodigo);
    painelCentral.add(Box.createRigidArea(new Dimension(0, 25)));

    // Painel com campo de entrada e botões
    JPanel cartaoEntrada = criarPainelCartao();
    cartaoEntrada.setLayout(new GridBagLayout());
    cartaoEntrada.setMaximumSize(new Dimension(460, 180));
    painelCentral.add(cartaoEntrada);
    painelCentral.add(Box.createRigidArea(new Dimension(0, 25)));

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(12, 12, 12, 12);
    c.anchor = GridBagConstraints.LINE_START;
    
    // Campo para o usuário digitar o código
    rotuloEntrada = new JLabel("Informe o código:");
    rotuloEntrada.setForeground(COR_TEXTO);
    rotuloEntrada.setFont(rotuloEntrada.getFont().deriveFont(16f));
    c.gridx = 0;
    c.gridy = 0;
    cartaoEntrada.add(rotuloEntrada, c);

    campoEntrada = new JTextField(3);
    campoEntrada.setFont(campoEntrada.getFont().deriveFont(16f));
    c.gridx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    cartaoEntrada.add(campoEntrada, c);

    // Botão de verificação
    botaoVerificar = new BotaoInterativo("Verificar Código");
    botaoVerificar.setFont(botaoVerificar.getFont().deriveFont(Font.BOLD, 18f));
    botaoVerificar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.anchor = GridBagConstraints.CENTER;
    cartaoEntrada.add(botaoVerificar, c);

    // Botão de tentativa após erro
    botaoTentarNovamente = new BotaoInterativo("Tente Novamente");
    botaoTentarNovamente.setFont(botaoTentarNovamente.getFont().deriveFont(Font.BOLD, 16f));
    botaoTentarNovamente.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    botaoTentarNovamente.setVisible(false);
    c.gridy = 2;
    cartaoEntrada.add(botaoTentarNovamente, c);

    // Resultado da verificação (mensagem colorida)
    infoResultado = new JLabel(" ");
    infoResultado.setForeground(new Color(0xD14343));
    infoResultado.setFont(infoResultado.getFont().deriveFont(Font.BOLD, 18f));
    infoResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
    painelCentral.add(infoResultado);
    painelCentral.add(Box.createRigidArea(new Dimension(0, 25)));

    // Temporizador visual circular
    painelTemporizador = new PainelTemporizador();
    painelTemporizador.setPreferredSize(new Dimension(120, 120));
    JPanel wrapperCentral = new JPanel(new FlowLayout(FlowLayout.CENTER));
    wrapperCentral.setOpaque(false);
    wrapperCentral.add(painelTemporizador);
    painelCentral.add(wrapperCentral);
    painelCentral.add(Box.createRigidArea(new Dimension(0, 40)));

    // Painel inferior com botão para abrir o banco
    JPanel painelRodape = new JPanel(new BorderLayout());
    painelRodape.setBorder(new EmptyBorder(20, 0, 20, 0));
    container.add(painelRodape, BorderLayout.SOUTH);

    JLabel rotuloBanco = new JLabel("Gerenciar banco de dados");
    rotuloBanco.setForeground(COR_TEXTO);
    rotuloBanco.setFont(rotuloBanco.getFont().deriveFont(16f));
    rotuloBanco.setHorizontalAlignment(SwingConstants.CENTER);
    painelRodape.add(rotuloBanco, BorderLayout.NORTH);

    // Botão para abrir o arquivo .db
    botaoAbrirBanco = new BotaoInterativo("Abrir banco de dados");
    botaoAbrirBanco.setFont(botaoAbrirBanco.getFont().deriveFont(16f));
    botaoAbrirBanco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    JPanel painelBotaoCentralizado = new JPanel(new FlowLayout(FlowLayout.CENTER));
    painelBotaoCentralizado.setOpaque(false);
    painelBotaoCentralizado.add(botaoAbrirBanco);
    painelRodape.add(painelBotaoCentralizado, BorderLayout.CENTER);

    // Ação do botão: abre o arquivo .db com o programa padrão
    botaoAbrirBanco.addActionListener(_ -> {
        try {
            Desktop.getDesktop().open(new java.io.File("autenticador.db"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo: " + ex.getMessage());
        }
    });

    // Inicializações principais
    conectarBancoDeDados();
    gerarCodigo();
    iniciarAtualizadorCodigo();
    iniciarTemporizador();

    // Ação do botão "Verificar Código"
    botaoVerificar.addActionListener(_ -> {
        String entrada = campoEntrada.getText().trim();
        ResultadoVerificacao resultado = verificarCodigo(entrada);
        if (resultado == ResultadoVerificacao.ACESSO_LIBERADO) {
            // Acesso correto
            infoResultado.setText("Acesso liberado!");
            infoResultado.setForeground(new Color(0, 128, 0));
            painelTemporizador.setVisible(false);
            botaoTentarNovamente.setVisible(false);
            limparMensagemAposAtraso();
        } else {
            // Acesso incorreto
            gerarCodigo();
            segundosRestantes = 60;
            infoResultado.setText("Acesso negado! Código alterado.");
            infoResultado.setForeground(new Color(0xD14343));
            botaoTentarNovamente.setVisible(true);
            painelTemporizador.setVisible(false);
            campoEntrada.setVisible(false);
            rotuloEntrada.setVisible(false);
            botaoVerificar.setVisible(false);
            limparMensagemAposAtraso();
        }
    });

    // Ação do botão "Tentar Novamente"
    botaoTentarNovamente.addActionListener(_ -> {
        campoEntrada.setText("");
        infoResultado.setText(" ");
        botaoTentarNovamente.setVisible(false);
        campoEntrada.setVisible(true);
        rotuloEntrada.setVisible(true);
        botaoVerificar.setVisible(true);
        painelTemporizador.setVisible(true);
    });
}

            // Estabelece a conexão com o banco de dados SQLite.
            // Cria a tabela 'codigos' caso ela ainda não exista.
            public void conectarBancoDeDados() {
            try {
            conexao = DriverManager.getConnection("jdbc:sqlite:autenticador.db");
            String sql = "CREATE TABLE IF NOT EXISTS codigos (codigo TEXT PRIMARY KEY, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        // Gera um número aleatório de 3 dígitos (entre 100 e 999).
        // Atualiza o rótulo da interface e salva o código no banco.
        public void gerarCodigo() {
        int numeroAleatorio = ThreadLocalRandom.current().nextInt(100, 1000);
        codigo = String.valueOf(numeroAleatorio);
        rotuloCodigo.setText("Código Gerado: " + codigo);
        salvarCodigoNoBanco(codigo);
    }

        // Insere ou substitui o código atual na tabela 'codigos'.
        // Se houver mais de 100 códigos, remove os mais antigos para manter o limite.
        public void salvarCodigoNoBanco(String codigo) {
        try {
            String sql = "INSERT OR REPLACE INTO codigos (codigo) VALUES (?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, codigo);
            stmt.executeUpdate();

            Statement countStmt = conexao.createStatement();
            ResultSet rs = countStmt.executeQuery("SELECT COUNT(*) FROM codigos");
            if (rs.next() && rs.getInt(1) > 100) {
                PreparedStatement deleteStmt = conexao.prepareStatement(
                    "DELETE FROM codigos WHERE codigo IN (SELECT codigo FROM codigos ORDER BY timestamp ASC LIMIT ?)"
                );
                deleteStmt.setInt(1, rs.getInt(1) - 100);
                deleteStmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        // Consulta no banco se o código digitado existe.
        // Retorna ACESSO_LIBERADO se existir, ou ACESSO_NEGADO caso contrário.
        public ResultadoVerificacao verificarCodigo(String entrada) {
        try {
            String sql = "SELECT codigo FROM codigos WHERE codigo = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, entrada);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? ResultadoVerificacao.ACESSO_LIBERADO : ResultadoVerificacao.ACESSO_NEGADO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultadoVerificacao.ACESSO_NEGADO;
        }
    }

        // A cada 60 segundos, gera um novo código e reinicia o temporizador.
        // Também limpa mensagens anteriores de resultado.
        public void iniciarAtualizadorCodigo() {
        Timer atualizador = new Timer(60000, _ -> {
            gerarCodigo();
            segundosRestantes = 60;
            painelTemporizador.setSegundosRestantes(segundosRestantes);
            painelTemporizador.setVisible(true);
            infoResultado.setText(" ");
        });
        atualizador.start();
    }
        // Temporizador que conta os segundos restante para inserir o código.
        // Atualiza o anel gráfico e o tempo restante na tela.
        public void iniciarTemporizador() {
        Timer contador = new Timer(1000, _ -> {
            if (segundosRestantes > 0) {
                segundosRestantes--;
                painelTemporizador.setSegundosRestantes(segundosRestantes);
                painelTemporizador.repaint();
            } else {
                painelTemporizador.setVisible(false);
            }
        });
        contador.start();
    }
        // Remove a mensagem de resultado (liberado/negado) após 3 segundos.
        private void limparMensagemAposAtraso() {
        Timer timer = new Timer(3000, _ -> infoResultado.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
        // Cria um painel estilizado com bordas arredondadas e fundo branco.
        // Usado como cartão para agrupar entrada e botões.
        private JPanel criarPainelCartao() {
        JPanel painel = new JPanel();
        painel.setBackground(Color.white);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E7EB), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        painel.setOpaque(true);
        return painel;
    }

        public static void main(String[] args) { // Define o tema visual com FlatLaf e inicia a interface do sistema.
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Falha ao inicializar FlatLaf: " + ex);
        }

        SwingUtilities.invokeLater(() -> {
            ProjetoA3 app = new ProjetoA3();
            app.setVisible(true);
        });
    }


        private static class BotaoInterativo extends JButton { 
        private final Color corHover = Color.WHITE;
        private final Color corPressionado = Color.GREEN;
        private final Color corNormal = new Color(0x1877F2);

        // Botão customizado com comportamento visual para hover e clique.  
        public BotaoInterativo(String texto) {
            super(texto);
            setContentAreaFilled(false);
            setOpaque(true);
            setBackground(corNormal);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
            setFocusPainted(false);
            inicializarEventos();
        }

        private void inicializarEventos() { // Define os efeitos visuais do botão ao passar o mouse ou clicar.
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBackground(corHover);
                    setForeground(corNormal);
                }

                public void mouseExited(MouseEvent e) {
                    setBackground(corNormal);
                    setForeground(Color.WHITE);
                }

                public void mousePressed(MouseEvent e) {
                    setBackground(corPressionado);
                    setForeground(Color.WHITE);
                }

                public void mouseReleased(MouseEvent e) {
                    setBackground(corHover);
                    setForeground(corNormal);
                }
            });
        }

        @Override
        public void setContentAreaFilled(boolean b) {
        }
    }


    private static class PainelTemporizador extends JPanel {
        private int segundosRestantes = 60;
        private static final int MAXIMO_SEGUNDOS = 60;
        private static final Color COR_FUNDO = new Color(0xE6E6E6);
        private static final Color COR_FRENTE = new Color(0x1877F2);

        public void setSegundosRestantes(int valor) {
            this.segundosRestantes = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {  // Desenha o anel de progresso com base no tempo restante.
            super.paintComponent(g);
            int tamanho = Math.min(getWidth(), getHeight());
            int espessura = 10;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = espessura / 2 + 5;
            int diametro = tamanho - 2 * padding;

            g2.setStroke(new BasicStroke(espessura, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(COR_FUNDO);
            g2.drawOval(padding, padding, diametro, diametro);

            double angulo = 360.0 * segundosRestantes / MAXIMO_SEGUNDOS;
            g2.setColor(COR_FRENTE);
            Arc2D.Double arco = new Arc2D.Double(padding, padding, diametro, diametro,
                    90 - angulo, angulo, Arc2D.OPEN);
            g2.draw(arco);

            String textoTempo = segundosRestantes + "s";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
            FontMetrics fm = g2.getFontMetrics();
            int larguraTexto = fm.stringWidth(textoTempo);
            int alturaTexto = fm.getAscent();
            int x = tamanho / 2 - larguraTexto / 2;
            int y = tamanho / 2 + alturaTexto / 2 - 4;

            g2.setColor(COR_CODIGO);
            g2.drawString(textoTempo, x, y);

            g2.dispose();
        }
    }
}
