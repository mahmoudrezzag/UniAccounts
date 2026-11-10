from reportlab.lib.pagesizes import A4
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Image, Table, TableStyle, PageBreak, KeepTogether
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.lib.units import cm
from PIL import Image as PI

ss = getSampleStyleSheet()
body = ParagraphStyle('b', parent=ss['Normal'], fontName='Helvetica', fontSize=10.2, leading=14.5, spaceAfter=5)
h1 = ParagraphStyle('h1', parent=ss['Heading1'], fontSize=15, textColor=colors.HexColor('#1a3d6d'), spaceBefore=10, spaceAfter=6)
h2 = ParagraphStyle('h2', parent=ss['Heading2'], fontSize=11.5, textColor=colors.HexColor('#1a3d6d'), spaceBefore=8, spaceAfter=3)
title = ParagraphStyle('t', parent=ss['Title'], fontSize=21, textColor=colors.HexColor('#1a3d6d'), spaceAfter=4)
sub = ParagraphStyle('s', parent=body, alignment=1, textColor=colors.HexColor('#444444'))
cap = ParagraphStyle('c', parent=body, fontSize=8.5, alignment=1, textColor=colors.HexColor('#555555'), leading=11)
code = ParagraphStyle('code', parent=body, fontName='Courier', fontSize=8.3, leading=10.6, backColor=colors.HexColor('#F3F4F6'),
                      borderPadding=5, spaceBefore=4, spaceAfter=8, leftIndent=4)

def img(path, width):
    w, h = PI.open(path).size
    return Image(path, width=width, height=width * h / w)

def footer(c, d):
    c.saveState(); c.setFont('Helvetica', 8); c.setFillColor(colors.grey)
    c.drawString(2*cm, 1.2*cm, 'UniAccounts - Work 1: Creational Design Patterns (Java)')
    c.drawRightString(A4[0]-2*cm, 1.2*cm, f'Page {d.page}'); c.restoreState()

doc = SimpleDocTemplate('Report_Work1_UniAccounts.pdf', pagesize=A4, leftMargin=2*cm, rightMargin=2*cm,
                        topMargin=1.8*cm, bottomMargin=1.8*cm, title='UniAccounts - Work 1 Report', author='Student')
S = []
S += [Paragraph('UniAccounts - Student Account Management', title),
      Paragraph('Practical Work (TP) - Design Patterns in Java<br/><b>Work 1: the core and the creational patterns</b>', sub),
      Paragraph('Student(s): ____________________ &nbsp;&nbsp;|&nbsp;&nbsp; Group: ________ &nbsp;&nbsp;|&nbsp;&nbsp; Date: ________', sub),
      Spacer(1, 8)]

S += [Paragraph('1. Project overview', h1),
      Paragraph('UniAccounts manages student accounts for a university. This first stage builds the <b>core</b>: a '
                '<font face="Courier">StudentAccount</font> class (8 fields, 3 mandatory and 5 optional), one central '
                '<font face="Courier">AccountRepository</font> and one <font face="Courier">AuditLogger</font>. '
                'Three creational patterns are used, each at the place where it solves a real problem: '
                '<b>Builder</b> to create the account, <b>Singleton</b> to guarantee a single repository and a single audit log, '
                'and <b>Prototype</b> to create the accounts of a whole batch by cloning a template.', body)]

t = Table([['Pattern', 'Classes', 'Package'],
           ['Builder', 'StudentAccount.Builder (builds StudentAccount)', 'model'],
           ['Singleton', 'AccountRepository, AuditLogger', 'repository, audit'],
           ['Prototype', 'Prototype<T>, StudentAccount.copy() / copyFor()', 'prototype, model']],
          colWidths=[3*cm, 9.5*cm, 4.5*cm])
t.setStyle(TableStyle([('BACKGROUND', (0,0), (-1,0), colors.HexColor('#1a3d6d')), ('TEXTCOLOR', (0,0), (-1,0), colors.white),
                       ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'), ('FONTSIZE', (0,0), (-1,-1), 9.2),
                       ('GRID', (0,0), (-1,-1), 0.4, colors.HexColor('#999999')),
                       ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor('#EEF3FA')]),
                       ('VALIGN', (0,0), (-1,-1), 'MIDDLE'), ('TOPPADDING', (0,0), (-1,-1), 4), ('BOTTOMPADDING', (0,0), (-1,-1), 4)]))
S += [t, Spacer(1, 4)]

S += [Paragraph('2. Class diagram (UML)', h1), img('uml.png', 13.2*cm),
      Paragraph('Figure 1 - UML class diagram: the three patterns and their relations. '
                '<i>StudentAccount</i> is the Product of the Builder and the Concrete Prototype; the two Singletons are '
                'used by the Client (<i>Main</i>); the repository uses the logger.', cap)]

S += [PageBreak(), Paragraph('3. The patterns: where, why, and what if absent', h1)]

S += [Paragraph('3.1 Builder - StudentAccount.Builder', h2),
      Paragraph('<b>Where?</b> The nested class <font face="Courier">StudentAccount.Builder</font>. The mandatory fields '
                '(<i>id, fullName, email</i>) are passed to the Builder constructor; optional fields (<i>phone, level, major, '
                'scholarship, courses</i>) are set through chained methods, each with its default value (null, L1, false, empty list). '
                '<font face="Courier">build()</font> checks all the rules and only then creates the account through a private constructor.', body),
      Paragraph('<b>Problem solved.</b> With eight fields, five of them optional, a single constructor with eight '
                'parameters is unreadable and error-prone: two String parameters can be swapped without any compiler warning '
                '(for example email and phone). The Builder makes every call self-documenting, lets us skip unwanted optional fields, '
                'and centralizes validation, so an invalid account (no id, e-mail without @, unknown level) can never exist.', body),
      Paragraph('<b>If we did not use it.</b> We would need either a telescoping constructor (many overloads) or '
                'a no-argument constructor with setters. The second option allows half-built, unvalidated objects.', body),
      Paragraph('new StudentAccount.Builder("20240002", "Sara Khelifi", "sara@univ.dz")<br/>'
                '&nbsp;&nbsp;&nbsp;&nbsp;.phone("0555123456").level("M1").major("Software Engineering")<br/>'
                '&nbsp;&nbsp;&nbsp;&nbsp;.scholarship(true).addCourse("Design Patterns").build();', code)]

S += [Paragraph('3.2 Singleton - AccountRepository and AuditLogger', h2),
      Paragraph('<b>Where?</b> Two classes: <font face="Courier">AccountRepository</font> (stores every account in a map and '
                'enforces the unique id) and <font face="Courier">AuditLogger</font> (records every operation). '
                'Both have a private constructor and a static <font face="Courier">getInstance()</font>, implemented with the '
                '<i>initialization-on-demand holder</i> idiom: the instance is created lazily, on the first call, and the JVM '
                'guarantees thread safety without synchronizing getInstance(). Their methods are <font face="Courier">synchronized</font> '
                'because they are shared mutable state.', body),
      Paragraph('<b>Problem solved.</b> The program must have one repository and one audit log. If every part of the program '
                'created its own, accounts would be scattered among several copies, uniqueness of the id could not be checked, '
                'and the audit trail would be split. The demo proves it: <i>r1 == r2</i> is true, and accounts saved through one reference '
                'are seen through the other.', body),
      Paragraph('<b>If we did not use it.</b> Passing the same object everywhere by hand is fragile, because one forgotten '
                '<i>new</i> silently creates a second repository, and the bug appears only as "missing" accounts.', body)]

S += [Paragraph('3.3 Prototype - Prototype&lt;T&gt; and StudentAccount.copy()', h2),
      Paragraph('<b>Where?</b> The interface <font face="Courier">Prototype&lt;T&gt;</font> declares <font face="Courier">copy()</font>; '
                '<font face="Courier">StudentAccount</font> implements it. A template account is built once for a batch '
                '(for example L2, Computer Science, courses Algorithms / Databases / Java OOP). For each newcomer, '
                '<font face="Courier">copyFor(id, name, email)</font> clones the template and changes only the identity '
                '(the phone number is reset, because it is personal).', body),
      Paragraph('<b>Problem solved.</b> Hundreds of students join the same level and major with the same courses every year. '
                'Rebuilding each account from scratch repeats the same level, major and course list hundreds of times. '
                'Cloning avoids this repetition. The copy is <b>deep</b>: the course list is duplicated, so adding '
                '"Machine Learning" to one student changes neither the template nor the other students (shown in Figure 3).', body),
      Paragraph('<b>If we did not use it.</b> We would copy the shared data by hand each time, with a risk of forgetting a field. '
                'A shallow copy would be worse: all students would share the same course list object, and one modification '
                'would leak to everybody.', body)]

S += [PageBreak(), Paragraph('4. Execution results', h1),
      Paragraph('The screenshots below show the real output of <font face="Courier">java com.uniaccounts.Main</font>.', body),
      img('shot1.png', 16.6*cm),
      Paragraph('Figure 2 - Builder (defaults, optional fields, rejected invalid data) and Singleton (same instance, duplicate id rejected).', cap),
      Spacer(1, 6), img('shot2.png', 16.6*cm),
      Paragraph('Figure 3 - Prototype (template cloned three times, deep-copy proof) and the final state with the single audit log.', cap)]

S += [Paragraph('5. Conclusion', h1),
      Paragraph('The three creational patterns work together: the <b>Builder</b> guarantees valid accounts, the '
                '<b>Prototype</b> produces batches of them cheaply, and the <b>Singleton</b> classes give them one place to live and '
                'one place to be audited. This core is the base that Work 2 will extend (state, observer, composite, adapter, etc.) '
                'without rewriting it.', body),
      Paragraph('<b>Sources:</b> course lecture notes on design patterns; Gamma, Helm, Johnson, Vlissides, <i>Design Patterns: '
                'Elements of Reusable Object-Oriented Software</i>; J. Bloch, <i>Effective Java</i> (Builder, Singleton items).', body)]

doc.build(S, onFirstPage=footer, onLaterPages=footer)
